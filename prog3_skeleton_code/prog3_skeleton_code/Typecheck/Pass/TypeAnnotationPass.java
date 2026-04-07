package Typecheck.Pass;
import Typecheck.Types.*;
import java.util.ArrayList;
import java.util.stream.*;
import Typecheck.TypeCheckException;

public class TypeAnnotationPass extends Pass<Void> {
   

    // Hint: Build the base type from the name, then wrap it for pointers and any [] modifiers.
    // 1. Construct the base type ("string" -> STRING)
    // 2. Wrap the base type in a POINTER Type if stars count > 0
    // 3. If the Array has concrete values ([10][3][9]), Then construct a LIST
    //         a. Loop over the List of brackets ([x][i][j]...)
    //            For the first bracket ([x]) construct a List of "basetype"
    //                  LIST(basetype, basetype, basetype,... x times)
    //            For the next bracket ([i]) construct a List of the PREVIOUS List
    //                  LIST(
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     ... i times
    //                  )
    //            Keep repeating until no more brackets
    // 4. If the Array does not have expressions ([][][]...), then construct an ARRAY
    //         a. Pull the first bracket ([]) and construct an ARRAY(basetype)
    //            Pull the next bracket and construct an ARRAY(ARRAY(basetype))
    //            Keep repeating.


   @Override
   public Void visitType(Absyn.Type node) {
       // Here is how I checked if the type needed ARRAY or a LIST:
       // Feel free to use it or change it. 
      boolean isARRAY = node.brackets.list.stream()
         .allMatch(e -> ((Absyn.ArrayType)e).size instanceof Absyn.EmptyExp);
      boolean isLIST = node.brackets.list.stream()
         .allMatch(e -> ((Absyn.ArrayType)e).size instanceof Absyn.DecLit);
      if (!isARRAY && !isLIST && node.brackets.list.size() != 0) 
         throw new TypeCheckException("Array has invalid parameters in []");

      Type basetype = node.name.equals("int") ? new INT() :
                  node.name.equals("string") ? new STRING() :
                  node.name.equals("void") ? new VOID() :
                  new ALIAS(node.name);
               
      for (int i = 0; i < node.pointerCount; i++) {
         basetype = new POINTER(basetype);
      }
      if (node.brackets.list.size() == 0) {
         node.typeAnnotation = basetype;
         return null;
      }
      if (isLIST) {
         Type current = basetype;
         for (int i = 0; i < node.brackets.list.size(); i++) {
            Absyn.ArrayType at = (Absyn.ArrayType) node.brackets.list.get(i);
            int size = ((Absyn.DecLit) at.size).value;
            ArrayList<Type> types = new ArrayList<>();
            for (int j = 0; j < size; j++) {
               types.add (current);
            }
            current = new LIST(types);
         }
         node.typeAnnotation = current;
      }
      else if (isARRAY && node.brackets.list.size() > 0) {
         Type current = basetype;
         for (int i = 0; i < node.brackets.list.size(); i++) {
            current = new ARRAY(current);
         }
         node.typeAnnotation = current;
      }
                  return null;

   }
   
} 
