package main;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GithubJop {
    private final GitHub github_token;
    private final Gui gui = new Gui();
    private Set<Long> allPrIds = new HashSet<>();

    public GithubJop() {

        try {
            github_token = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = github_token.getMyself();
        String myLogin =  myself.getLogin();
        int delayBeforeFirstStart = 1000;
        int interval = 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    boolean notificationFirstPR = !allPrIds.isEmpty();
                    HashSet<GHPullRequest> newPrqsts = new HashSet<>();

                    List<RepositoryDescription> listReposDescriptions = myself.getRepositories()
                            .values()
                            .stream()
                            .map(repository -> {
                                try {
                                    List<GHPullRequest> ghPullRequests = repository.queryPullRequests()
                                            .list()
                                            .toList();
                                    Set<Long> prIds = ghPullRequests.stream().map(GHPullRequest::getId).collect(Collectors.toSet());
                                    prIds.removeAll(allPrIds);
                                    allPrIds.addAll(prIds);

                                    ghPullRequests.forEach(pr -> {
                                        if (prIds.contains(pr.getId())) {
                                            newPrqsts.add(pr);
                                        }
                                    });

                                    return new RepositoryDescription(
                                            repository.getFullName(),
                                            repository,
                                            ghPullRequests);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

                    gui.setMenu(myLogin, listReposDescriptions);

                    if (notificationFirstPR) {
                        newPrqsts.forEach(pr -> {
                            gui.showNotification("New PullReq in " + pr.getRepository().getFullName(),
                                    pr.getTitle());
                        });
                    }
                }
                catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }, delayBeforeFirstStart, interval);
    }
}
