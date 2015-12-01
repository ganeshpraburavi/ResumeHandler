package resumeuploader;

import resumeuploader.uploader.Uploader;
import resumeuploader.watcher.ResumeDirectoryWatcher;
import java.io.IOException;
import java.util.Properties;

public class ResumeUploader {

    final String resumeUploaderPropertiesFileName = "resumeuploader.properties";

    ResumeDirectoryWatcher resumeDirectoryWatcher;
    Uploader fileUploader;
    Properties resumeUploaderProperties = new Properties();

    public ResumeUploader() throws IOException, InterruptedException{
        System.out.println(ResumeUploader.class.getClassLoader().getResource(resumeUploaderPropertiesFileName).getPath());
        resumeUploaderProperties.load(ResumeUploader.class.getClassLoader().getResourceAsStream(resumeUploaderPropertiesFileName));

        String resumePath = resumeUploaderProperties.getProperty("resumes_directory");
        String fileUploaderClass = resumeUploaderProperties.getProperty("uploader_class");

        try {
            fileUploader = (Uploader)Class.forName(fileUploaderClass).newInstance();
        }
        catch (Exception e){
            System.out.println("Uploader class not found in class path : " + fileUploaderClass);
            e.printStackTrace();
        }

        resumeDirectoryWatcher = new ResumeDirectoryWatcher(resumePath, fileUploader);
        resumeDirectoryWatcher.start();
    }

    public static void main(String args[]) throws IOException,InterruptedException
    {
        new ResumeUploader();
    }
}
