package com.example.eam.Procurement.Service;

import com.example.eam.Procurement.Entity.ProcurementNumberSequence;
import com.example.eam.Procurement.Repository.ProcurementNumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class NumberGeneratorService {

    private final ProcurementNumberSequenceRepository repository;

    @Transactional
    public String generateMrNumber() {
        return generate("MR");
    }

    @Transactional
    public String generatePoNumber() {
        return generate("PO");
    }

    @Transactional
    public String generateGrnNumber() {
        return generate("GRN");
    }

    private String generate(String prefix) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                return doGenerate(prefix);
            } catch (DataIntegrityViolationException ex) {
                if (attempt == 2) {
                    throw ex;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate number for " + prefix);
    }

    private String doGenerate(String prefix) {
        int year = Year.now(ZoneOffset.UTC).getValue();
        ProcurementNumberSequence sequence = repository.findBySequenceKeyAndYear(prefix, year)
                .orElseGet(() -> repository.save(
                        ProcurementNumberSequence.builder()
                                .sequenceKey(prefix)
                                .year(year)
                                .lastValue(0L)
                                .build()
                ));

        sequence.setLastValue(sequence.getLastValue() + 1);
        repository.save(sequence);

        long value = sequence.getLastValue();
        if (value > 9999) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sequence exhausted for prefix " + prefix);
        }
        return String.format("%s-%04d-%04d", prefix, year, value);
    }
}
