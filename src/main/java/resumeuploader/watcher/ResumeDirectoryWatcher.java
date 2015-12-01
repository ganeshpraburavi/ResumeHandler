package resumeuploader.watcher;

import resumeuploader.uploader.Uploader;

import java.io.IOException;

public class ResumeDirectoryWatcher extends DirectoryWatcher {

    private Uploader uploader;

    public ResumeDirectoryWatcher(String resumeDir, Uploader uploader) throws IOException, InterruptedException {
        super(resumeDir);
        this.uploader = uploader;
    }

    public void process(String path) {
        // TODO Should be moved out of this place.
        System.out.println("Uploading file: " + path);
        if (uploader.uploadFile(path))
            System.out.println("Uploading Complete for " + path);
        else
            System.out.println("Uploading failed for " + path);
    }
}
