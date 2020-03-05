import React from 'react';
import JarrodView from "./JarrodView";
import Container from '@material-ui/core/Container';
import StocksView from "./StocksView";
import AddStock from "./AddStock";

function App() {
    return (
        <Container maxWidth="sm">
            <JarrodView/>
            <StocksView />
            <AddStock />
        </Container>
    );
}

export default App;
