package ai.fin.service;

import java.io.OutputStream;

public interface TransactionExportService {

    /**
     * Write all transactions of the given user as UTF-8 CSV (with BOM) to the stream.
     * The caller owns the stream and is responsible for closing it.
     *
     * @param userId       user identifier
     * @param outputStream output stream to write to
     */
    void exportAsCsv(String userId, OutputStream outputStream);
}
