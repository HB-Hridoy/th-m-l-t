-verbose

-keep public class com.hridoy.thmlt.ThMLT {
    public *;
}
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 10
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses com.hridoy.thmlt.repack
-flattenpackagehierarchy
-dontpreverify