"use client";

import React, { useState, useEffect } from "react";
import { Select, ComboboxItem, Group, Text } from "@mantine/core";
import { IJ2NLanguagesProps, ILanguage } from "./J2NLanguages.type";
import "flag-icons/css/flag-icons.min.css";
import "./J2NLanguages.css";

const DEFAULT_LANGUAGES: ILanguage[] = [
  { code: "en", name: "English", flagCode: "us" },
  { code: "vi", name: "Tiếng Việt", flagCode: "vn" },
  { code: "kr", name: "Korean", flagCode: "kr" },
  { code: "jp", name: "Japanese", flagCode: "jp" },
  { code: "cn", name: "Chinese", flagCode: "cn" },
];

const J2NLanguages: React.FC<IJ2NLanguagesProps> = ({
  languages = DEFAULT_LANGUAGES,
  defaultLanguage = "en",
  onChange,
  className,
}) => {
  const [value, setValue] = useState<string | null>(defaultLanguage);

  useEffect(() => {
    const storedLanguage = localStorage.getItem("language");
    if (
      storedLanguage &&
      languages.some((lang) => lang.code === storedLanguage)
    ) {
      setValue(storedLanguage);
    }
  }, []);

  // Find current language object
  const currentLanguage = languages.find((lang) => lang.code === value);

  const handleChange = (val: string | null) => {
    if (val) {
      setValue(val);
      localStorage.setItem("language", val);
      if (onChange) {
        onChange(val);
      }
    }
  };

  // Custom render for options in dropdown
  const renderOption = ({ option }: { option: ComboboxItem }) => {
    const language = languages.find((lang) => lang.code === option.value);
    if (!language) return null;
    return (
      <Group gap="sm">
        <span className={`fi fi-${language.flagCode}`} />
        <Text size="sm">{language.name}</Text>
      </Group>
    );
  };

  return (
    <div className={`j2n-language-select ${className || ""}`}>
      <Select
        data={languages.map((lang) => ({
          value: lang.code,
          label: lang.name,
        }))}
        value={value}
        onChange={handleChange}
        renderOption={renderOption}
        leftSection={
          currentLanguage ? (
            <span className={`fi fi-${currentLanguage.flagCode}`} />
          ) : null
        }
        leftSectionPointerEvents="none"
        allowDeselect={false}
        checkIconPosition="right"
        withCheckIcon={true}
        w="fit-content"
        variant="unstyled"
        comboboxProps={{ width: "max-content", withinPortal: false }}
        styles={{
          input: {
            paddingLeft: "24px",
            paddingRight: "0",
            color: "transparent",
            width: "50px",
            textAlign: "center",
            backgroundColor: "transparent",
            border: "none",
          },
          section: {
            pointerEvents: "none",
            width: "24px",
          },
        }}
      />
    </div>
  );
};

export default J2NLanguages;
