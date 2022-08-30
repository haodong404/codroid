import { title } from "process";
import { PreferencesProps } from "../components/preferences";
import {
  Setting,
  SettingCategory,
} from "../components/preferences/setting-items/props";

export const textEditorPreference: PreferencesProps = {
  title: "TextEditorTextEditorTextEditor",
  settings: {
    "Accessibility Page Size": {
      category: SettingCategory.Input,
      title: "Accessibility Page Size",
      summary:
        "Controls the number of lines in the editor that can be read out by a screen reader at once. When we detect a screen reader we automatically set the default to be 500. Warning: this has a performance implication for numbers larger than the default.",
      defaultValue: "10",
    },
    "Accessibility Support": {
      category: SettingCategory.Dropdown,
      title: "Accessibility Support",
      summary:
        "Controls whether the editor should run in a mode where it is optimized for screen readers. Setting to on will disable word wrapping.",
      options: ["auto", "off", "on"],
      defaultValue: 2,
    },
    "Auto Closing Brackets": {
      category: SettingCategory.Dropdown,
      title: "Auto Closing Brackets",
      summary:
        "Controls whether the editor should automatically close brackets after the user adds an opening bracket.",
      options: ["always", "language defined", "before whitespace", "never"],
      defaultValue: 0,
    },
    "Bracket Pair Colorization": {
      category: SettingCategory.Switch,
      title:
        "Bracket Pair Colorization: Independent Color Pool Per Bracket Type",
      defaultValue: false,
    },
    "Column Selection": {
      category: SettingCategory.Switch,
      title: "Column Selection",
      summary:
        "Enable that the selection with the mouse and keys is doing column selection.",
      default: true,
    },
    "Code Actions On Save": {
      category: SettingCategory.Textarea,
      title: "Code Actions On Save",
      summary: "Code action kinds to be run on save.",
    },
  },
};

export const generalSetting: PreferencesProps = {
  title: "通用",
  settings: {
    "Accessibility Page Size": {
      category: SettingCategory.Input,
      title: "无障碍页面大小",
      summary:
        "控制编辑器中可被屏幕阅读器一次性读出的行数。当我们检测到一个屏幕阅读器时，我们会自动将默认值设置为500。警告：对于大于默认值的数字，这对性能会有影响。",
    },
    "Accessibility Support": {
      category: SettingCategory.Dropdown,
      title: "无障碍支持",
      summary:
        "控制编辑器是否应该在为屏幕阅读器优化的模式下运行。设置为 on 将禁用文字包装。",
      options: ["自动", "关闭", "开启"],
      defaultValue: 2,
    },
    "Auto Closing Brackets": {
      category: SettingCategory.Dropdown,
      title: "自动闭合括号",
      summary: "控制编辑器是否应该在用户添加开括号后自动关闭括号。",
      options: ["总是", "根据语言", "空字符之后", "从不"],
      defaultValue: 0,
    },
    "Bracket Pair Colorization": {
      category: SettingCategory.Switch,
      title: "括号着色: 每种括号都有独立的颜色每种括号都有独立的颜色",
      defaultValue: false,
    },
    "Column Selection": {
      category: SettingCategory.Switch,
      title: "可选择列",
      summary: "启用鼠标和按键的选择是做列选择。",
      default: true,
    },
    "Code Actions On Save": {
      category: SettingCategory.Textarea,
      title: "代码保存时执行操作",
      summary: "代码在保存是执行的操作种类",
    },
  },
};

export const preferencesMock = {
  TextEditor: textEditorPreference,
  General: generalSetting,
};
