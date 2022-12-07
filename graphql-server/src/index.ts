import {ApolloServer} from '@apollo/server';
import {startStandaloneServer} from '@apollo/server/standalone';
import { startServerAndCreateLambdaHandler } from "@as-integrations/aws-lambda";

import games from './extract-bgg-games-details.json' assert {type: 'json'};


// A schema is a collection of type definitions (hence "typeDefs")
// that together define the "shape" of queries that are executed against
// your data.
const typeDefs = `#graphql
# Comments in GraphQL strings (such as this one) start with the hash (#) symbol.

# This "Game" type defines the queryable fields for every game in our data source.
type Game {
    bggId: Int,
    name: String,
    description: String,
    thumbnailUrl: String,
    imageUrl: String,
    minPlayers: Int,
    maxPlayers: Int,
    yearPublished: Int,
    publisher: String,
    designer: String,
    artist: String,
    honors: String,
    bestPlayers: [Int],
    goodPlayers: [Int],
    avgRating: Float,
    avgWeight: Float
}

# The "Query" type is special: it lists all of the available queries that
# clients can execute, along with the return type for each. In this
# case, the "games" query returns an array of zero or more Games (defined above).
type Query {
    hello: String,
    game (id: Int!): Game,
    games (minR: Float, minW: Float, sortBy: String, limit: Int, offset: Int): [Game]
}
`;

// Resolvers define how to fetch the types defined in your schema.
// This resolver retrieves games from the "games" array above.
const resolvers = {
    Query: {
        hello: () => 'BGG Snapshot',
        game(parent, args, contextValue, info) {
            return games.find(game => game.bggId === args.id)
        },
        games(parent, args, contextValue, info) {
            let _games = [...games];
            if (args.minR) {
                _games = _games.filter(game => (game.avgRating || 0) > args.minR);
            }
            if (args.minW) {
                _games = _games.filter(game => (game.avgWeight || 0) > args.minW);
            }
            if (args.sortBy) {
                if (args.sortBy == 'W' || args.sortBy == 'w') {   // by weight
                    _games = _games.sort((game1, game2) => {
                        const w1 = game1.avgWeight || 0;
                        const w2 = game2.avgWeight || 0;
                        return w1 > w2 ? -1 : w1 < w2 ? 1 : 0;
                    });
                } else if (args.sortBy == 'R' || args.sortBy == 'r') {  // by rating
                    _games = _games.sort((game1, game2) => {
                        const r1 = game1.avgRating || 0;
                        const r2 = game2.avgRating || 0;
                        return r1 > r2 ? -1 : r1 < r2 ? 1 : 0;
                    });
                } else if (args.sortBy == 'N' || args.sortBy == 'n') {     // by name
                    _games = _games.sort((game1, game2) => {
                        return game1.name > game2.name ? 1 : game1.name < game2.name ? -1 : 0;
                    });
                } else {
                    console.error("Unexpected sortBy argument: " + args.sortBy);
                }
            }
            if (args.offset) {
                _games = _games.slice(args.offset);
            }
            if (args.limit) {
                _games = _games.slice(0, args.limit);
            }
            return _games;
        }
    },
};

// The ApolloServer constructor requires two parameters: your schema
// definition and your set of resolvers.
const server = new ApolloServer({
    typeDefs,
    resolvers,
});

///// Passing an ApolloServer instance to the `startStandaloneServer` function:
/////  1. creates an Express app
/////  2. installs your ApolloServer instance as middleware
/////  3. prepares your app to handle incoming requests
//TODO: Uncomment bellow for a local test
// const {url} = await startStandaloneServer(server, {
//     listen: {port: 4000},
// });
// console.log(`🚀  Server ready at: ${url}`);

//TODO: Uncomment bellow for the AWS Lambda deployment with Serverless
export const graphqlHandler = startServerAndCreateLambdaHandler(server);

