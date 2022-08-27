import { Component, ComponentChild, h } from "preact";
import SvgIcon from "../SvgIcon";
import styles from "./index.module.css";

export class SearchBar extends Component<{ class?: string }> {
  render(): ComponentChild {
    return (
      <div class={`${styles.searchbar_root} ${this.props.class}`}>
        <input
          class="relative top-0 left-0 bottom-0 right-0 focus:outline-0 flex-1 mr-2 bg-transparent"
          placeholder="Searching for settings."
        ></input>
        <SvgIcon
          name="search"
          class="h-7 w-7 fill-primary-800"
        ></SvgIcon>
      </div>
    );
  }
}
