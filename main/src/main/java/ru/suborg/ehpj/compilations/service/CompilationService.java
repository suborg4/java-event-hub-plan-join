package ru.suborg.ehpj.compilations.service;

import ru.suborg.ehpj.compilations.dto.CompilationDto;
import ru.suborg.ehpj.compilations.dto.NewCompilationDto;
import ru.suborg.ehpj.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);

    void deleteCompilation(Long compilationId);
}
