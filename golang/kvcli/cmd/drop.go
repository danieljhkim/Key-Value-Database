/*
Copyright © 2025 NAME HERE <EMAIL ADDRESS>
*/
package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var dropCmd = &cobra.Command{
	Use:   "drop",
	Short: "Remove all keys from the database",
	Args:  cobra.NoArgs,
	Run: func(cmd *cobra.Command, args []string) {
	    fmt.Printf("Not implemented yet.")
	    return
		response, err := kvClient.ExecuteCommand("KV CLEAR")
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	// rootCmd.AddCommand(dropCmd)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// dropCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// dropCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
