package ru.suborg.ehpj.compilations;

import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.compilations.dto.CompilationDto;
import ru.suborg.ehpj.compilations.dto.NewCompilationDto;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                newCompilationDto.getTitle(),
                newCompilationDto.getPinned()
        );
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
