package minimized;

public class TabIndented {
//           ^^^^^^^^^^^ definition minimized/TabIndented# public class TabIndented
//           ^^^^^^^^^^^ definition minimized/TabIndented#`<init>`(). public TabIndented()
→public void app() {
//           ^^^ definition minimized/TabIndented#app(). public void app()
→→Object o = new Object() {
//^^^^^^ reference java/lang/Object#
//       ^ definition local0 Object o
//               ^^^^^^ reference java/lang/Object#
→→→@Override
//  ^^^^^^^^ reference java/lang/Override#
→→→public boolean equals(Object other) {
//                ^^^^^^ definition local3 @Override public boolean equals(Object other)
//                       ^^^^^^ reference java/lang/Object#
//                              ^^^^^ definition local5 Object other
→→→→return false;
→→→}

→→→@Override
//  ^^^^^^^^ reference java/lang/Override#
→→→public String toString() {
//        ^^^^^^ reference java/lang/String#
//               ^^^^^^^^ definition local4 @Override public String toString()
→→→→return "";
→→→}
→→};
→}
}
