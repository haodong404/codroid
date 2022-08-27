import { Component, ComponentChild, h } from "preact";
import Dropdown from "../dropdown";
import { DropdownItem, SettingItem } from "./props";

export default class DropdownSetting extends Component<
  DropdownItem | SettingItem
> {
  render(): ComponentChild {
    return (
      <div>
        <h1 class="font-semibold mb-0.5">{this.props.title}</h1>
        <p class="text-neutral-700 leading-none text-sm">
          {this.props.subtitle}
        </p>
        <div class="mt-2">
          <Dropdown />
        </div>
      </div>
    );
  }
}
