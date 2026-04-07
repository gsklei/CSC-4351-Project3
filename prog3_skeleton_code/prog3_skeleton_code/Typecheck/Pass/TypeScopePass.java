package Typecheck.Pass;
import Typecheck.Types.*;
import Typecheck.SymbolTable.*;
import java.util.ArrayList;
import java.util.Set;

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
      if (node.body != null) {
         node.body.accept(this);
      }
      return null;
   }
// Hint: Unions define a type that can be any of their member types.
// 1. Visit the body so member types are resolved.
// 2. Collect each member's typeAnnotation.
// 3. Build an OR type from them.
// 4. Register the union name in the current scope.
   @Override
   public Void visitUnionDecl(Absyn.UnionDecl node) {
      if (node.body != null) {
         node.body.accept(this);
      }
      return null;
   }
// Hint: Typedef introduces a new name for an existing type.
// Visit the type first, then register the alias in the current scope.
   @Override
   public Void visitTypedef(Absyn.Typedef node) {
      if (node.type != null) {
         node.type.accept(this);
      }
      return null;
   }
// Hint: Replace ALIAS types with their real definition.
// Remember that Types can be nested (IE ARRAY(ARRAY(ARRAY(...))) )
// Traverse the whole type to search for Aliases. Once an alias is found,
// look up the type of the alias in the symbol table.
    // This is a function I found helpful to implement. If you have a solution
    // in mind that does not include a helper function, then feel free to ignore
   private Type resolveAlias(Type type) {
      // Base case: null or primitive type with no synonyms to resolve
      if (type == null) return null;

      // If this type has synonyms (i.e. it's an alias), resolve to the real type
      Set<Type> synonyms = type.getSynonyms();
      if (synonyms.size() > 1 || !synonyms.contains(type)) {
         for (Type synonym : synonyms) {
               if (!synonym.equals(type)) {
                  // Recurse in case the synonym is itself an alias
                  return resolveAlias(synonym);
               }
         }
      }

      return type;
   }


   // Hint: Visit the brackets and resolve the alias to a type (if the typeAnnotation contains ALIAS)
   @Override
   public Void visitType(Absyn.Type node) {
      // Just ensure brackets are visited
      if (node.brackets != null) {
         visit(node.brackets);
      }
      return null;
   }

}
