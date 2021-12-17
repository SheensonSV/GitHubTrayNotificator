package main;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;

public class RepositoryDescription {
    private String name;
    private GHRepository ghRepository;
    private List<GHPullRequest> ghpr;

    public RepositoryDescription(String name, GHRepository ghRepository, List<GHPullRequest> ghpr) {
        this.name = name;
        this.ghRepository = ghRepository;
        this.ghpr = ghpr;
    }

    public String getName() {
        return name;
    }

    public GHRepository getGhRepository() {
        return ghRepository;
    }

    public List<GHPullRequest> getGhpr() {
        return ghpr;
    }
}
