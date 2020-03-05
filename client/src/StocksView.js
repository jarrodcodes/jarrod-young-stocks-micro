import React from 'react';
import StockCard from "./StockCard";
import {initws} from "./WebSocket";

const stocks = {};

class StocksView extends React.Component {
    constructor(props) {
        super(props);
        this.state = ({loading: true});
    }

    componentWillMount() {
    }

    componentDidMount() {
        const ws = initws();
        ws.onmessage = (message) => {
            console.log(message);
            this.setState({loading: false});
            const data = JSON.parse(message.data);
            stocks[data.symbol] = data;
        };
    }

    render() {

        return (
            Object.values(stocks).map(value => {
                console.log(value);
                return (
                    <StockCard {...value} />
                )
            })
        )
    }
}

export default StocksView