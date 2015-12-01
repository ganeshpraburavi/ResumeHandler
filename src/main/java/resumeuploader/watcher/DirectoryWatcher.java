package resumeuploader.watcher;

import java.nio.file.*;
import java.io.*;

abstract class DirectoryWatcher
{
    Path dir;
    File folderDir;

    public DirectoryWatcher(String directory) {
        dir = Paths.get(directory);
        folderDir = new File(directory);
    }

    public void start() throws IOException,InterruptedException
    {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        WatchKey originalKey = dir.register(watcher,StandardWatchEventKinds.ENTRY_CREATE);

        for(;;){
            WatchKey key = watcher.take();

            for(WatchEvent<?> event: key.pollEvents()){
                String newPath = folderDir+"/"+event.context();
                process(newPath);
            }

            boolean valid = key.reset();
            if(!valid){}
        }
    }

    abstract public void process(String path);
}