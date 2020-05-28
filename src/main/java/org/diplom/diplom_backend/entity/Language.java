package org.diplom.diplom_backend.entity;

import lombok.Data;
import org.diplom.diplom_backend.constant.LanguageConstant;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Language {
    @Id
    String _id;
    String language;
    List<String> images;

    public Language() {
        this._id = UUID.randomUUID().toString();
    }

    public Language(LanguageConstant language, List<Image> images) {
        this();
        this.language = language.toString();
        this.images = images.stream().map(Image::get_id).collect(Collectors.toList());
    }

    public Language(String language, List<Image> images) {
        this();
        this.language = language;
        this.images = images.stream().map(Image::get_id).collect(Collectors.toList());
    }
}
