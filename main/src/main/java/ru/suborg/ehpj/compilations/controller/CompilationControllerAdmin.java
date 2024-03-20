package ru.suborg.ehpj.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.suborg.ehpj.compilations.dto.CompilationDto;
import ru.suborg.ehpj.compilations.dto.NewCompilationDto;
import ru.suborg.ehpj.compilations.dto.UpdateCompilationRequest;
import ru.suborg.ehpj.compilations.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilation) {
        return compilationService.updateCompilation(compilationId, updateCompilation);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compilationId) {
        compilationService.deleteCompilation(compilationId);
    }
}
