import { Appbar } from "./components/appbar";
import { h } from "preact";
import { SearchBar } from "./components/searchbar";
import colors from "tailwindcss/colors";
import { Navigator } from "./components/navigator";
import { ItemProps } from "./components/navigator-item";
import Preferences from "./components/preferences";

export function Preference() {
  const navigatorItems: Array<ItemProps> = [
    {
      title: "Text Editor",
      icon: "code",
      color: colors.red,
    },
    {
      title: "General",
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
  return (
    <>
      <Appbar class="sticky top-0"></Appbar>
      <SearchBar class="mt-2"></SearchBar>
      <Navigator class="mt-4" items={navigatorItems}></Navigator>
      <Preferences class="mt-4" title="Text Editor" />
    </>
  );
}
