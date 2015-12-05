package resumeuploader.storage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import resumeuploader.database.PersistFileURL;

public class GoogleDriveStorager implements Storager {

    private final String APPLICATION_NAME = "Student Worker";
    private final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/GoogleDriveStorager");
    private FileDataStoreFactory DATA_STORE_FACTORY;
    private final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private HttpTransport HTTP_TRANSPORT;
    private Drive service;

    private Properties googleDriveProperties;
    private String googleDrivePropertyFileName = "google_drive_uploader.properties";


    public GoogleDriveStorager() throws IOException {
        googleDriveProperties = new Properties();

        try {
            googleDriveProperties.load(getClass().getClassLoader().getResourceAsStream(googleDrivePropertyFileName));
        } catch (Exception e) {
            System.out.println(googleDrivePropertyFileName + " not found");
            e.printStackTrace();
        }
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        service = getDriveService();
    }

    private final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPS_READONLY);

    private File insertFile(Drive service, String title, String description,
                            String parentId, String mimeType, String filename) {
        File body = new File();
        java.io.File fileContent = new java.io.File(filename);
        String name = fileContent.getName();
        String asset_key = name.replaceFirst("[.][^.]+$", "");

        body.setTitle(asset_key+".pdf");
        body.setDescription(description);
        body.setMimeType(mimeType);
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }

        
        
        
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        try {
            File file = service.files().insert(body, mediaContent).execute();
            
            file.setShareable(true);

            System.out.println("File ID that should be associated with the student: " + file.getId());

            Permission permissionToPrabu = new Permission();

            permissionToPrabu.setValue("me");
            permissionToPrabu.setType("anyone");
            permissionToPrabu.setRole("reader");

            service.permissions().insert(file.getId(),permissionToPrabu).execute();
            System.out.println("Shareable link to give for your professor: " + file.getAlternateLink());
            PersistFileURL persistURL = new PersistFileURL();
            persistURL.save(asset_key, file.getAlternateLink());
            System.out.println("Saved "+asset_key+" to database");
            fileContent.delete();
            return file;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

    public Credential authorize() throws IOException {
        InputStream in =
                GoogleDriveStorager.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void listFiles(Drive service) throws IOException {
        FileList result = service.files().list()
                .setMaxResults(10)
                .execute();
        List<File> files = result.getItems();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            }
        }
    }

    public Boolean storeFile(String path) {
        Long fileName = Math.abs(new Random().nextLong());

        if(insertFile(service, fileName+".pdf","" , "", "", path) != null)
            return true;
        return false;
    }
}