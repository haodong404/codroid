import {
  Setting,
  SettingType,
} from "../components/preferences/setting-items/props";

const textEditorSettings = {
  auto: {
    category: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    defaultValue: 0,
    options: [
      "Aaaa",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Auto",
      "a",
      "org.codroid.textmate.TextMate",
    ],
  },
  22: {
    category: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    defaultValue: 3,
    options: [
      "Aaaa",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Auto",
      "a",
      "org.codroid.textmate.TextMate",
    ],
  },
  333: {
    category: SettingType.Switch,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    defaultValue: false,
  },
  444: {
    category: SettingType.Input,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    defaultValue: "Default value",
    placeholder: "Please enter",
  },
  short_switch: {
    category: SettingType.Switch,
    title: "Shorter",
    defaultValue: false,
  },
};
export default textEditorSettings;
