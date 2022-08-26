import { Component, ComponentChild, h } from "preact";
import searchIcon from "@assets/search.svg";

export class SearchBar extends Component<{ class?: string }> {
  render(): ComponentChild {
    return (
      <div
        class={`w-full bg-neutral-200 hover:bg-neutral-100 transition-all duration-300 pl-6 pr-6 pt-2 pb-2 flex items-center rounded-full ${this.props.class}`}
      >
        <input
          class="relative top-0 left-0 bottom-0 right-0 focus:outline-0 flex-1 mr-2 bg-transparent"
          placeholder="Searching for settings."
        ></input>
        <img src={searchIcon} class="h-5 text-gray-400"></img>
      </div>
    );
  }
}
