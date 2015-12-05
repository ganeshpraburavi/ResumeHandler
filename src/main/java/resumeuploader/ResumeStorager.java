package resumeuploader;

import resumeuploader.storage.Storager;
import resumeuploader.watcher.ResumeDirectoryWatcher;
import java.io.IOException;
import java.util.Properties;

public class ResumeStorager {

    final String resumeUploaderPropertiesFileName = "resumeuploader.properties";

    ResumeDirectoryWatcher resumeDirectoryWatcher;
    Storager fileUploader;
    Properties resumeStoragerProperties = new Properties();

    public ResumeStorager() throws IOException, InterruptedException{
        System.out.println(ResumeStorager.class.getClassLoader().getResource(resumeUploaderPropertiesFileName).getPath());
        resumeStoragerProperties.load(ResumeStorager.class.getClassLoader().getResourceAsStream(resumeUploaderPropertiesFileName));

        String resumePath = resumeStoragerProperties.getProperty("resumes_directory");
        String fileUploaderClass = resumeStoragerProperties.getProperty("uploader_class");

        try {
            fileUploader = (Storager)Class.forName(fileUploaderClass).newInstance();
        }
        catch (Exception e){
            System.out.println("Storager class not found in class path : " + fileUploaderClass);
            e.printStackTrace();
        }

        resumeDirectoryWatcher = new ResumeDirectoryWatcher(resumePath, fileUploader);
        resumeDirectoryWatcher.start();
    }

    public static void main(String args[]) throws IOException,InterruptedException
    {
        new ResumeStorager();
    }
}
