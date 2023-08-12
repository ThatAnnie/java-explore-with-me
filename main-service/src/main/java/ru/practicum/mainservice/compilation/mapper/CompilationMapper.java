package ru.practicum.mainservice.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.event.model.Event;

import java.util.List;

@Mapper
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", source = "newCompilationDto.pinned", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Compilation newDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> events);

    CompilationDto toCompilationDto(Compilation compilation);

}
