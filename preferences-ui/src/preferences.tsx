import { Appbar } from "./components/appbar";
import { Component, ComponentChild, h } from "preact";
import { SearchBar } from "./components/searchbar";
import colors from "tailwindcss/colors";
import { Navigator } from "./components/navigator";
import { NavigatorItemProps } from "./components/navigator-item";
import Preferences from "./components/preferences";
import {
  Setting,
  SettingType,
} from "./components/preferences/setting-items/props";

const textEditorSettings: Array<Setting> = [
  {
    type: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: 0,
    items: [
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
  {
    type: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: 3,
    items: [
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
  {
    type: SettingType.Switch,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: false,
  },
  {
    type: SettingType.Input,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: "Default value",
    placeholder: "Please enter",
  },
  {
    type: SettingType.Textarea,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value:
      "Default value Controls whether the editor should remove adjacent closing quotes or brackets when deleting.Controls whether the editor should remove adjacent closing quotes or brackets when deleting.Controls whether the editor should remove adjacent closing quotes or brackets when deleting.Controls whether the editor should remove adjacent closing quotes or brackets when deleting.Controls whether the editor should remove adjacent closing quotes or brackets when deleting.Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    placeholder: "Please enter",
  },
];

const navigatorItems: Array<NavigatorItemProps> = [
  {
    title: "Text Editor",
    icon: "code",
    color: colors.red,
    selected: true,
  },
  {
    title: "Security",
    icon: "setting",
    color: colors.yellow,
  },
  {
    title: "General",
    icon: "setting",
    color: colors.green,
  },
  {
    title: "General",
    icon: "setting",
    color: colors.yellow,
  },
];

export default class Preference extends Component {
  state = {
    current: 0,
  };

  changePreference = (index: number) => {
    this.setState({
      current: index,
    });
  };

  render(): ComponentChild {
    return (
      <>
        <Appbar class="sticky top-0"></Appbar>
        <SearchBar class="mt-2"></SearchBar>
        <Navigator
          class="mt-4"
          items={navigatorItems}
          onChanged={this.changePreference}
        ></Navigator>
        {h(Preferences, {
          title: navigatorItems[this.state.current].title!,
          settings: textEditorSettings,
        })}
      </>
    );
  }
}
