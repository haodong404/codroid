import { Component, ComponentChild, h } from "preact";

export class Button extends Component<{ class?: string }> {
  render(): ComponentChild {
    return (
      <div
        class={`select-none cursor-pointer bg-red-100 hover:bg-red-50 transition-colors duration-300 border-2 rounded-3xl px-4 py-0.5 border-red-900 ${this.props.class}`}
      >
        <span class="select-none text-xs font-sans font-semibold text-red-900">
          RESET ALL
        </span>
      </div>
    );
  }
}
