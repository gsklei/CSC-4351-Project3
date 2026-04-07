package Typecheck.Pass;
import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;
import Absyn.*;

public class CreateScopePass extends Pass<Void> {

   protected Scope currentscope;
   public Scope globalscope;

   public CreateScopePass() {
      this.globalscope = new Scope();
      this.currentscope = globalscope;
   }

   @Override
   public Void visitFunDecl(FunDecl node) {
      Scope previous = currentscope;
      currentscope = new Scope(currentscope);
      visit(node.type);
      visit(node.params);
      visit(node.body);
      node.scope = currentscope;
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitStructDecl(StructDecl node) {
      Scope previous = currentscope;
      currentscope = new Scope(currentscope);
      visit(node.body);
      node.scope = currentscope;
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitUnionDecl(UnionDecl node) {
      Scope previous = currentscope;
      currentscope = new Scope(currentscope);
      visit(node.body);
      node.scope = currentscope;
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitIfStmt(IfStmt node) {
      Scope previous = currentscope;
      currentscope = new Scope(currentscope);
      visit(node.expression);
      visit(node.if_statement);
      visit(node.else_statement);
      node.scope = currentscope;
      currentscope = previous;
      return null;
   }

   @Override
   public Void visitWhileStmt(WhileStmt node) {
      Scope previous = currentscope;
      currentscope = new Scope(currentscope);
      visit(node.expression);
      visit(node.statement);
      node.scope = currentscope;
      currentscope = previous;
      return null;
   }

}