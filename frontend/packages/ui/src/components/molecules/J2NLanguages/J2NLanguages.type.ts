import "flag-icons/css/flag-icons.min.css";

// Interface for language object
export interface ILanguage {
  code: string;
  name: string;
  flagCode: string; // e.g., 'us', 'vn'
}

// Props interface
export interface IJ2NLanguagesProps {
  languages?: ILanguage[];
  defaultLanguage?: string;
  onChange?: (language: string) => void;
  className?: string;
}
