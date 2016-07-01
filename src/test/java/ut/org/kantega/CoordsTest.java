package ut.org.kantega;

import junit.framework.Assert;
import org.junit.Test;
import org.kantega.documenter.MavenCoordinates;

import static fj.data.List.arrayList;

public class CoordsTest {

    @Test
    public void correctnames(){
        String coords = "no.nte.service:sms-rest:1.3";
        String coords2 = "no.nte.service.vannstand:vanstand.read-core:1.6";

        Assert.assertEquals("Parsing feilet ikke",true,MavenCoordinates.fromString("","","").isFail());
        Assert.assertEquals("Parsing feilet ikke",true,MavenCoordinates.fromString(null,"","").isFail());

        MavenCoordinates coordinates = MavenCoordinates.fromString(coords,"","pom").success();
        Assert.assertEquals(
          "path er ikke korrekt",
          arrayList("no","nte","service","sms-rest","1.3","sms-rest-1.3.pom"),
          coordinates.toPath()
          );

        MavenCoordinates coordinates2 = MavenCoordinates.fromString(coords2,"doc","json").success();
        Assert.assertEquals(
          "path er ikke korrekt",
          arrayList("no","nte","service","vannstand","vanstand.read-core","1.6","vanstand.read-core-1.6-doc.json"),
          coordinates2.toPath()
        );

    }

}
