import { Appbar } from "./components/appbar";
import { h } from "preact";
import { SearchBar } from "./components/searchbar";

export function Preference() {
  return (
    <div class="pl-2 pr-2">
      <Appbar></Appbar>
      <SearchBar class="mt-2"></SearchBar>
    </div>
  );
}
