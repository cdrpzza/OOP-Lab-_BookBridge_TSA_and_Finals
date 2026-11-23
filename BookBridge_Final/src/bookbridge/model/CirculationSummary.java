package bookbridge.model;

import java.util.Map;

public record CirculationSummary(
        int availableBooks,
        int checkedOutBooks,
        int activeLoans,
        int pendingReservations,
        Map<MembershipType, Long> loansByType,
        long totalEstimatedFines
) {
    public CirculationSummary {
        loansByType = Map.copyOf(loansByType);
    }
}