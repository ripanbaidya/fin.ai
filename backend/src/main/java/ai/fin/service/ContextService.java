package ai.fin.service;

public interface ContextService {

    String buildBudgetContext(String userId);

    String buildSavingsGoalContext(String userId);
}
