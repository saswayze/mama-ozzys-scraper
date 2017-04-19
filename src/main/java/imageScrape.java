import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.URL;

/*** Created by samuel.swayze on 12/6/2016. ***/
public class imageScrape {

    public static void main(String[] args) throws Exception {
        URL imageUrl = new URL("https://lh3.googleusercontent.com/-7-udB1EKvDM/Vj-Hvj_KvYI/AAAAAAAACDs/oOVyeCETocI/s640/blogger-image-1225543699.jpg");
        String path = System.getProperty("user.home") + "\\Desktop\\newDir";
        new File(path).mkdirs();
        //System.out.println(System.getProperty("user.home");
        File image = new File(path + "\\image.jpg");
        FileUtils.copyURLToFile(imageUrl, image);
    }

}
