import * as React from "react"
import {ChakraProvider, theme, Grid, GridItem} from "@chakra-ui/react"

import {SearchGraph} from "./components/SearchGraph"
import {VisualGraph} from "./components/VisualGraph"
import {DetailGraph} from "./components/DetailGraph"

export function App() {
    return (
        <ChakraProvider theme={theme}>
            <Grid
                templateAreas={`"header header"
                              "main detail"`}
                gridTemplateRows={'50px 1fr'}
                gridTemplateColumns={'1fr 300px'}
                fontWeight='bold'
            >
                <GridItem p={1} area={'header'}>
                    <SearchGraph/>
                </GridItem>
                <GridItem p={1}  area={'main'}>
                    <VisualGraph/>
                </GridItem>
                <GridItem p={1} area={'detail'}>
                    <DetailGraph/>
                </GridItem>
            </Grid>
        </ChakraProvider>
    )
}


