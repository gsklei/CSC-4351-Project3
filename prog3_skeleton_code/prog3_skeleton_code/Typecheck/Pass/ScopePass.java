package Typecheck.Pass;
import Absyn.*;
import Typecheck.SymbolTable.*;

public class ScopePass<T> extends Pass<T> {

   protected Scope currentscope;
	protected T defaultReturn = null;

    // Hint: Save scope → switch to node.scope → visit children → restore scope.

   public ScopePass(Scope s) {
      this.currentscope = s;
   }

   @Override
   public T visitFunDecl(FunDecl node) {
   }

   @Override
	public T visitStructDecl(StructDecl node) {
	}

	@Override
	public T visitUnionDecl(UnionDecl node) {
	}

	@Override
	public T visitIfStmt(IfStmt node) {
	}

   @Override
	public T visitWhileStmt(WhileStmt node) {
	}

}
