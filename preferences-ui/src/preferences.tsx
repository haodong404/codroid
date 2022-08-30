import { Appbar } from "./components/appbar";
import { Component, ComponentChild, h } from "preact";
import { SearchBar } from "./components/searchbar";
import colors from "tailwindcss/colors";
import { Navigator } from "./components/navigator";
import { NavigatorItemProps } from "./components/navigator-item";
import Preferences from "./components/preferences";
import { PreferencesProps } from "./components/preferences";
import { preferencesMock } from "./mock/settings";

const navigatorItems: Array<NavigatorItemProps> = [];
const preferences: Array<PreferencesProps> = [];

if (import.meta.env.PROD) {
  const allPreferences: any = JSON.parse(PreferencesInjection.allPreferences());
  let isFirst = true;
  let fromCodroid = true;
  for (const [key, value] of Object.entries(allPreferences)) {
    // meet a divider
    if (key == "DIVIDER") {
      fromCodroid = false;
      continue;
    }
    navigatorItems.push({
      title: (value as any).title,
      id: key,
      fromCodroid: fromCodroid,
      icon: "code",
      color: colors.red,
      selected: isFirst,
    });
    preferences.push({
      title: (value as any).title,
      settings: (value as any).settings,
    });
    if (isFirst) {
      isFirst = false;
      PreferencesInjection.selectPreference(key, true);
    }
  }
} else {
  let isFirst = true;
  for (const [key, value] of Object.entries(preferencesMock)) {
    navigatorItems.push({
      title: value.title,
      id: key,
      fromCodroid: true,
      icon: "setting",
      color: colors.amber,
      selected: isFirst,
    });
    preferences.push({
      title: value.title,
      settings: value.settings,
    });
    isFirst = false;
  }
}
export default class Preference extends Component {
  state = {
    preference: preferences[0],
  };

  componentDidMount() {}

  changePreference = (index: number) => {
    this.setState({
      preference: preferences[index],
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
        {h(Preferences, this.state.preference as PreferencesProps)}
      </>
    );
  }
}
