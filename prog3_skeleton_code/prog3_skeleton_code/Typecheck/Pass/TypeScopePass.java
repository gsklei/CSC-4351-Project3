package Typecheck.Pass;
import Typecheck.Types.*;
import Typecheck.SymbolTable.*;
import java.util.ArrayList;

public class TypeScopePass extends ScopePass<Void> {

   public TypeScopePass(Scope s) {
      super(s);
   }

// Hint: Structs define a new type from their member types.
// 1. Visit the body so member types are fully resolved.
// 2. Collect each member's typeAnnotation.
// 3. Build a LIST type from them.
// 4. Register the struct name in the current scope.
   @Override
   public Void visitStructDecl(Absyn.StructDecl node) {
      visit(node.body);
      ArrayList<Type> memberTypes = new ArrayList<>();
      for (Absyn.Decl d : node.body.list) {
         Absyn.StructMember m = (Absyn.StructMember) d;
         memberTypes.add(m.type.typeAnnotation);
      }
      LIST structType = new LIST(memberTypes);
      this.currentscope.addType(node.name, new TypeSymbol(node.name, structType));
      return null;
   }

// Hint: Unions define a type that can be any of their member types.
// 1. Visit the body so member types are resolved.
// 2. Collect each member's typeAnnotation.
// 3. Build an OR type from them.
// 4. Register the union name in the current scope.
   @Override
   public Void visitUnionDecl(Absyn.UnionDecl node) {
      visit(node.body);
      ArrayList<Type> memberTypes = new ArrayList<>();
      for (Absyn.Decl d : node.body.list) {
         Absyn.UnionMember m = (Absyn.UnionMember) d;
         memberTypes.add(m.type.typeAnnotation);
      }
      OR unionType = new OR(memberTypes);
      this.currentscope.addType(node.name, new TypeSymbol(node.name, unionType));
      return null;
   }

// Hint: Typedef introduces a new name for an existing type.
// Visit the type first, then register the alias in the current scope.
   @Override
   public Void visitTypedef(Absyn.Typedef node) {
      visit(node.type);
      this.currentscope.addType(node.name, new TypeSymbol(node.name, node.type.typeAnnotation));
      return null;
   }

// Hint: Replace ALIAS types with their real definition.
   private void resolveAlias(Type type) {
      if (type instanceof ALIAS) {
         ALIAS alias = (ALIAS) type;
         if (this.currentscope.hasType(alias.name)) {
            alias.setType(this.currentscope.getType(alias.name).type);
         } else {
            throw new Typecheck.TypeCheckException("Unknown type: " + alias.name);
         }
      } else if (type instanceof ARRAY) {
         resolveAlias(((ARRAY) type).type);
      } else if (type instanceof POINTER) {
         resolveAlias(((POINTER) type).type);
      } else if (type instanceof LIST) {
         for (Type t : ((LIST) type).typelist) {
            resolveAlias(t);
         }
      } else if (type instanceof OR) {
         for (Type t : ((OR) type).options) {
            resolveAlias(t);
         }
      }
   }

// Hint: Visit the brackets and resolve the alias to a type
   @Override
   public Void visitType(Absyn.Type node) {
      visit(node.brackets);
      if (node.typeAnnotation != null) {
         resolveAlias(node.typeAnnotation);
      }
      return null;
   }

}