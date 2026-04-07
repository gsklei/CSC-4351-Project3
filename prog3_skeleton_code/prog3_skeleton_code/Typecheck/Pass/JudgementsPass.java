package Typecheck.Pass;
import Typecheck.Types.*;
import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;
import java.util.ArrayDeque;
import java.util.Deque;

public class JudgementsPass extends ScopePass<Void> {

   private Deque<Type> returnTypeStack = new ArrayDeque<>();

   public JudgementsPass(Scope s) {
      super(s);
   }

   @Override
   public Void visitFunDecl(Absyn.FunDecl node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      returnTypeStack.push(node.type.typeAnnotation);
      visit(node.params);
      visit(node.body);
      returnTypeStack.pop();
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitReturnStmt(Absyn.ReturnStmt node) {
      Type expected = returnTypeStack.peek();
      if (expected instanceof VOID)
         throw new TypeCheckException("void function cannot have a return statement");
      visit(node.expression);
      if (!expected.canAccept(node.expression.typeAnnotation))
         throw new TypeCheckException("Return type mismatch");
      return null;
   }

   @Override
   public Void visitVarDecl(Absyn.VarDecl node) {
      visit(node.type);
      Type declared = node.type.typeAnnotation;
      if (!(node.init instanceof Absyn.EmptyExp)) {
         visit(node.init);
         if (!declared.canAccept(node.init.typeAnnotation))
            throw new TypeCheckException("Type mismatch in declaration of " + node.name);
      }
      return null;
   }

   @Override
   public Void visitBinOp(Absyn.BinOp node) {
      visit(node.left);
      visit(node.right);
      if (!new INT().canAccept(node.left.typeAnnotation) || !new INT().canAccept(node.right.typeAnnotation))
         throw new TypeCheckException("Math operation requires numbers");
      node.typeAnnotation = new INT();
      return null;
   }

   @Override
   public Void visitFunExp(Absyn.FunExp node) {
      visit(node.name);
      String funName = ((Absyn.ID) node.name).value;
      FunSymbol fun = currentscope.getFun(funName);
      if (fun.params.typelist.size() != node.params.list.size())
         throw new TypeCheckException("Wrong number of arguments to " + funName);
      for (int i = 0; i < node.params.list.size(); i++) {
         visit(node.params.list.get(i));
         if (!fun.params.typelist.get(i).canAccept(node.params.list.get(i).typeAnnotation))
            throw new TypeCheckException("Argument " + i + " type mismatch in call to " + funName);
      }
      node.typeAnnotation = fun.returnType;
      return null;
   }

   @Override
   public Void visitIfStmt(Absyn.IfStmt node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.expression);
      if (!new INT().canAccept(node.expression.typeAnnotation))
         throw new TypeCheckException("If condition must be a number");
      visit(node.if_statement);
      visit(node.else_statement);
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitWhileStmt(Absyn.WhileStmt node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.expression);
      if (!new INT().canAccept(node.expression.typeAnnotation))
         throw new TypeCheckException("While condition must be a number");
      visit(node.statement);
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitUnaryExp(Absyn.UnaryExp node) {
      visit(node.exp);
      Type t = node.exp.typeAnnotation;
      switch (node.prefix) {
         case "*" -> {
            if (!(t instanceof POINTER p))
               throw new TypeCheckException("* operator requires a pointer operand");
            node.typeAnnotation = p.type;
         }
         case "&" -> node.typeAnnotation = new POINTER(t);
         default -> {
            if (!new INT().canAccept(t))
               throw new TypeCheckException("Unary operator requires a number");
            node.typeAnnotation = new INT();
         }
      }
      return null;
   }

   @Override
   public Void visitArrayExp(Absyn.ArrayExp node) {
      visit(node.name);
      Type t = node.name.typeAnnotation;
      for (Absyn.Exp e : node.index_list.list) {
         visit(e);
         if (!new INT().canAccept(e.typeAnnotation))
            throw new TypeCheckException("Array index must be a number");
         if (t instanceof LIST l)
            t = l.typelist.get(0);
         else if (t instanceof ARRAY a)
            t = a.type;
         else
            throw new TypeCheckException("Subscript on non-array type");
      }
      node.typeAnnotation = t;
      return null;
   }

   @Override
   public Void visitAssignExp(Absyn.AssignExp node) {
      visit(node.left);
      visit(node.right);
      if (!node.left.typeAnnotation.canAccept(node.right.typeAnnotation))
         throw new TypeCheckException("Assignment type mismatch");
      node.typeAnnotation = node.left.typeAnnotation;
      return null;
   }

   @Override
   public Void visitDecLit(Absyn.DecLit node) {
      node.typeAnnotation = new INT();
      return null;
   }

   @Override
   public Void visitStrLit(Absyn.StrLit node) {
      node.typeAnnotation = new STRING();
      return null;
   }

   @Override
   public Void visitID(Absyn.ID node) {
      if (currentscope.hasVar(node.value)) {
         node.typeAnnotation = currentscope.getVar(node.value).type;
      } else if (currentscope.hasFun(node.value)) {
         node.typeAnnotation = currentscope.getFun(node.value).returnType;
      } else {
         throw new TypeCheckException("Undefined identifier: " + node.value);
      }
      return null;
   }
}