import { Component, ComponentChild, createRef, Fragment, h } from "preact";
import { NavigatorItemProps, NavigatorItem } from "../navigator-item";

interface NavigatorProps {
  class?: string;
  items?: Array<NavigatorItemProps>;
  onChanged?: (index: number) => void;
}

function navigatorItems(props: any) {
  return (
    <>
      {props.items.map((item: NavigatorItemProps, index: number) => (
        <Fragment key={index}>{h(NavigatorItem, item)}</Fragment>
      ))}
    </>
  );
}

export class Navigator extends Component<NavigatorProps> {
  items: Array<NavigatorItemProps> = [];

  state = {
    lastSelected: 0,
  };

  constructor() {
    super();
  }

  componentWillMount() {
    if (this.props.items) {
      this.items = this.props.items.map(
        (item: NavigatorItemProps, index: number) => {
          return {
            ...item,
            onclick: (e: Event) => {
              if (import.meta.env.PROD) {
                PreferencesInjection.selectPreference(
                  item.id,
                  item.fromCodroid
                );
              }
              this.itemClick(index);
            },
          };
        }
      );
    }
  }

  itemClick = (index: number) => {
    if (this.state.lastSelected != index) {
      this.items[index].selected = true;
      this.items[this.state.lastSelected].selected = false;
      this.setState({
        lastSelected: index,
      });
      this.props.onChanged?.call(null, index);
    }
  };

  render(): ComponentChild {
    return (
      <div
        class={`scroll-smooth flex overflow-auto flex-nowrap pb-2 gap-2 ${this.props.class}`}
      >
        {h(navigatorItems, {
          items: this.items,
        })}
      </div>
    );
  }
}
