package bank.app.model.enums;

public enum TransactionStatus {
    INITIALIZED,
    PROCESSING,
    PENDING_CONFIRMATION,
    SECURITY_CHECK,
    PENDING_FUNDS,
    PROCESSED,
    COMPLETED,
    DECLINED,
    FAILED,
    CANCELLED
}