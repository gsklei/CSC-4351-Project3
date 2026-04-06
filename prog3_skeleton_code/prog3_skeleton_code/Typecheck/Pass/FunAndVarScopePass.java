package Typecheck.Pass;
import Typecheck.Types.*;
import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;
import java.util.ArrayList;

public class FunAndVarScopePass extends ScopePass<Void> {

   public FunAndVarScopePass(Scope s) {
      super(s);
   }
// Hint: Parameters behave like variables inside the function scope.
// 1. Ensure no function with this name already exists in the current scope.
// 2. Add the parameter as a variable symbol.
// 3. Use the parameter's typeAnnotation as its type.
   @Override
	public Void visitParameter(Absyn.Parameter node) {

       // Here is some code I used. You might find it useful:
       if (this.currentscope.hasLocalFun(node.name)) {
           throw new TypeCheckException("Tried to define var ("+node.name+") but fun with same name already exists");
       }
       
	}
// Hint: Functions must be registered in the current scope before visiting their body.
// 1. Ensure no variable with the same name exists in the current scope.
// 2. Collect the types of all parameters.
// 3. Construct the function type (parameter types → return type).
// 4. Add the function symbol to the current scope.
// 5. Enter the function’s scope and visit its contents.
   @Override
   public Void visitFunDecl(Absyn.FunDecl node) {
   }
// Hint: Struct members are variables within the struct's scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the member as a variable symbol using its annotated type.
   @Override
   public Void visitStructMember(Absyn.StructMember node) {
   }
// Hint: Union members behave like variables within the union scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the member as a variable symbol using its annotated type.

    // Note: This is only true for now. Union's will get special treatement
    // later, but for now we treat them as the same as structs. 
   @Override
   public Void visitUnionMember(Absyn.UnionMember node) {
   }
// Hint: Variable declarations introduce a new variable in the current scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the variable symbol using its annotated type.
   @Override
   public Void visitVarDecl(Absyn.VarDecl node) {
   }

}
