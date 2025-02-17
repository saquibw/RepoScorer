package com.scorer.repo.dto;

public enum Language {
	Java ("java"),
    Cobol ("cobol"),
    Python ("python"),
    JavaScript ("javascript"),
    C ("c"),
    Cpp ("cpp"),
    Go ("go"),
    Kotlin ("kotlin"),
    Ruby ("ruby"),
    Swift ("swift"),
    TypeScript ("typescript"),
    Scala ("scala"),
    Rust ("rust"),
    Perl ("perl"),
    PHP ("php"),
    Shell ("shell"),
    HTML ("html"),
    CSS ("css");

    private final String name;
    Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Language fromString(String text) {
        if (text == null) {
            return null;
        }
        for (Language language : Language.values()) {
            if (language.name.equalsIgnoreCase(text.trim())) {
                return language;
            }
        }
        return null;
    }
}
