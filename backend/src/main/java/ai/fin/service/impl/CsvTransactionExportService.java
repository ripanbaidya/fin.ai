package ai.fin.service.impl;

import ai.fin.entities.Transaction;
import ai.fin.export.TransactionCsvColumn;
import ai.fin.repository.TransactionRepository;
import ai.fin.service.TransactionExportService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.CsvExportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvTransactionExportService implements TransactionExportService {

    private static final int PAGE_SIZE = 500;
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"));

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(TransactionCsvColumn.headers())
            .build();

    private final TransactionRepository transactionRepository;

    @Override
    public void exportAsCsv(String userId, OutputStream outputStream) {
        log.debug("Starting CSV export for userId: {}", userId);

        try {
            outputStream.write(UTF8_BOM); // Excel compatibility

            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT);

            long exported = write(userId, printer);
            printer.flush();

            log.info("Exported {} transactions for userId: {}", exported, userId);
        } catch (IOException e) {
            log.error("CSV export failed for userId: {}", userId, e);
            throw new CsvExportException(ErrorCode.CSV_EXPORT_FAILED);
        }
    }

    private long write(String userId, CSVPrinter printer) throws IOException {
        long total = 0;
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, SORT_ORDER);
        Slice<Transaction> slice;

        do {
            slice = transactionRepository.findByUser_Id(userId, pageable);
            for (Transaction transaction : slice.getContent()) {
                printer.printRecord(toRow(transaction));
            }

            // Push each page to the client, keeps memory flat
            printer.flush();
            total += slice.getNumberOfElements();
            pageable = slice.nextPageable();
        } while (slice.hasNext());

        return total;
    }

    private Object[] toRow(Transaction transaction) {
        return Arrays.stream(TransactionCsvColumn.values()).map(column -> column.valueOf(transaction)).toArray();
    }
}
