declare const PreferencesInjection: {
  allPreferences: () => string;
  selectPreference: (id: string, fromCodroid: boolean) => void;

  putString: (key: string, value: string) => void;
  putInt: (key: string, value: number) => void;
  putBoolean: (key: string, value: boolean) => void;

  getString: (key: string) => string;
  getInt: (key: string) => number;
  getBoolean: (key: string) => boolean;
};
