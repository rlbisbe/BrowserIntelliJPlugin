import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowInBrowserClass extends AnAction {


    String getRepositoryUrl(File folder){

        File root = getRepositoryFolder(folder);

        File[] infoFile = root.listFiles((dir, name) -> name.contains("config"));

        String content = null;

        try {
            content = Files.toString(infoFile[0], Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pattern pattern = Pattern.compile("https:\\/\\/github.com\\/(.*).git");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()){
            return matcher.group(1);
        }

        return "";
    }


    File getRepositoryFolder(File folder){

        File[] gitFolder = folder.listFiles((dir, name) -> name.contains(".git"));

        if (gitFolder != null && gitFolder.length != 0){
            return gitFolder[0];
        }

        return getRepositoryFolder(folder.getParentFile());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

        String fileName = e.getData(PlatformDataKeys.PSI_FILE).getVirtualFile().toString();

        File file = null;
        try {
            file = new File(new URI(fileName));
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        File repositoryFolder = getRepositoryFolder(file.getParentFile());
        String repositoryUrl = getRepositoryUrl(repositoryFolder);
        String path = file.toString().replace(repositoryFolder.getParentFile().toString(), "");

        String url = "https://github.com/" + repositoryUrl + "/blob/master" + path;

        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
