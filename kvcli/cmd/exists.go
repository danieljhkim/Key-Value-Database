/*
Copyright © 2025 NAME HERE <EMAIL ADDRESS>
*/
package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

// existsCmd represents the exists command
var existsCmd = &cobra.Command{
	Use:   "exists [key]",
	Short: "Check if key exists",
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
	    fmt.Printf("Not yet implemented: %s\n", args[0])
	    return
		response, err := kvClient.ExecuteCommand("KV EXISTS " + args[0])
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	// rootCmd.AddCommand(existsCmd)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// existsCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// existsCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
