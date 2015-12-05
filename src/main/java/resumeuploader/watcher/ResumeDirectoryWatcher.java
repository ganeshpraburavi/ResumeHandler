package resumeuploader.watcher;

import resumeuploader.storage.Storager;

import java.io.IOException;

public class ResumeDirectoryWatcher extends DirectoryWatcher {

    private Storager uploader;

    public ResumeDirectoryWatcher(String resumeDir, Storager uploader) throws IOException, InterruptedException {
        super(resumeDir);
        this.uploader = uploader;
    }

    public void process(String path) {
        // TODO Should be moved out of this place.
        System.out.println("Uploading file: " + path);
        if (uploader.storeFile(path))
            System.out.println("Uploading Complete for " + path);
        else
            System.out.println("Uploading failed for " + path);
    }
}
