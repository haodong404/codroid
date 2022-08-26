import { Component, ComponentChild, h } from "preact";

export class Button extends Component<{ class?: string }> {
  render(): ComponentChild {
    return (
      <div
        class={`select-none cursor-pointer bg-red-100 hover:bg-red-50 transition-colors duration-300 border-2 rounded-3xl pl-3 pr-3 pt-px pb-px border-red-900 ${this.props.class}`}
      >
        <span class="select-none text-xs font-sans font-bold text-red-900">
          RESET ALL
        </span>
      </div>
    );
  }
}
