package Typecheck.Pass;
import Absyn.*;
import Typecheck.SymbolTable.*;

public class ScopePass<T> extends Pass<T> {

   protected Scope currentscope;
   protected T defaultReturn = null;

   public ScopePass(Scope s) {
      this.currentscope = s;
   }

   @Override
   public T visitFunDecl(FunDecl node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.type);
      visit(node.params);
      visit(node.body);
      currentscope = previous;
      return defaultReturn;
   }

   @Override
   public T visitStructDecl(StructDecl node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.body);
      currentscope = previous;
      return defaultReturn;
   }

   @Override
   public T visitUnionDecl(UnionDecl node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.body);
      currentscope = previous;
      return defaultReturn;
   }

   @Override
   public T visitIfStmt(IfStmt node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.expression);
      visit(node.if_statement);
      visit(node.else_statement);
      currentscope = previous;
      return defaultReturn;
   }

   @Override
   public T visitWhileStmt(WhileStmt node) {
      Scope previous = currentscope;
      currentscope = node.scope;
      visit(node.expression);
      visit(node.statement);
      currentscope = previous;
      return defaultReturn;
   }

}