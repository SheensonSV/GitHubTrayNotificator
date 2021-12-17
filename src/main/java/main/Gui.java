package main;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Gui {

    private final TrayIcon trayIcon;
    private final PopupMenu popup = new PopupMenu();

    public Gui() {
        try {
            SystemTray systemTray = SystemTray.getSystemTray();

            Image image = Toolkit
                    .getDefaultToolkit()
                    .createImage(getClass().getResource("/logo.png"));

            trayIcon = new TrayIcon(image, "GitHub_Helper");
            trayIcon.setImageAutoSize(true);
//            trayIcon.setToolTip("GitHub_Helper");

            systemTray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMenu(String login, List<RepositoryDescription> repos) {

        MenuItem menuItem = new MenuItem(login);
        menuItem.addActionListener((event) -> openInBrowser("https://github.com/" + login));

        MenuItem notificationItem = new MenuItem("notifications");
        notificationItem.addActionListener((event) -> openInBrowser("https://github.com/notifications"));

        Menu repositoriesMenu = new Menu("Repositories");
        repos
                .forEach(repo -> {
                    String name = repo.getGhpr().size() > 0
                            ? String.format("(%d) %s", repo.getGhpr().size(), repo.getName())
                            : repo.getName();
                    Menu repoSubMenu = new Menu(name);

                    MenuItem openInBrowserMI = new MenuItem("Open in Browser");

                    openInBrowserMI.addActionListener(e -> openInBrowser(repo.getGhRepository().getHtmlUrl().toString()));

                    if (repo.getGhpr().size() > 0) {
                        repoSubMenu.addSeparator();
                    }

                    repo.getGhpr().forEach(pr -> {
                        MenuItem prMI = new MenuItem(pr.getTitle());
                        prMI.addActionListener(e -> openInBrowser(pr.getHtmlUrl().toString()));
                        repoSubMenu.add(prMI);
                    });

                    repoSubMenu.add(openInBrowserMI);
                    repositoriesMenu.add(repoSubMenu);
                });

        popup.add(menuItem);
        popup.addSeparator();
        popup.add(notificationItem);
        popup.add(repositoriesMenu);

        trayIcon.setPopupMenu(popup);
    }

    public void openInBrowser(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void showNotification(String title, String text){
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
