import { Component, ComponentChild, Fragment, h } from "preact";
import colors from "tailwindcss/colors";
import { ItemProps, NavigatorItem } from "../navigator-item";

interface NavigatorProps {
  class?: string;
  items?: Array<ItemProps>;
}

function items(props: NavigatorProps) {
  return (
    <>
      {props.items?.map((item) => (
        <Fragment key={item}>{h(NavigatorItem, item)}</Fragment>
      ))}
    </>
  );
}

export class Navigator extends Component<NavigatorProps> {
  render(): ComponentChild {
    return (
      <div
        class={`scroll-smooth flex overflow-auto flex-nowrap pb-2 gap-4 ${this.props.class}`}
      >
        {h(items, this.props)}
      </div>
    );
  }
}
