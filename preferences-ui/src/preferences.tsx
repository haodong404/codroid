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
import { PreferencesProps } from "./components/preferences";
import textEditorSettings from "./mock/settings";


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
    preference: {
      title: "loading",
      settings: new Map<string, Setting>(),
    },
  };

  componentDidMount() {
    if (import.meta.env.DEV) {
      this.setState({
        preference: {
          title: "TITLE",
          settings: textEditorSettings,
        },
      });
    } else {
      this.setState({
        preference: JSON.parse(Android.json()),
      });
    }
  }

  changePreference = (index: number) => {
    this.setState({
      current: index,
    });
  };

  render(): ComponentChild {
    console.log(this.state.preference.title);

    return (
      <>
        <Appbar class="sticky top-0"></Appbar>
        <SearchBar class="mt-2"></SearchBar>
        <Navigator
          class="mt-4"
          items={navigatorItems}
          onChanged={this.changePreference}
        ></Navigator>
        {h(Preferences, this.state.preference as PreferencesProps)}
      </>
    );
  }
}
