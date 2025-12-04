package goodLinter.domain;

import goodLinter.datasource.Model;

public interface Check {
    public boolean doCheck(Model model);
    public String getCheckResult();
}
